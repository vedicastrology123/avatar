import fs from 'fs';
import { html as toVNode } from 'satori-html';
import satori from 'satori';
import { Resvg } from '@resvg/resvg-js';
import express from 'express';
import { fileURLToPath } from 'url';
import { dirname, join } from 'path';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const app = express();
app.use(express.json({ limit: '5mb' }));

const AUTH_TOKEN = "ibh7JSXJPdWu4DBq";

const satoriCustomCss = `
    .page-container {
        display: flex;
        flex-direction: column;
        width: 1200px;
        height: 1080px;
        background-color: #ffffff;
    }
    .chart-grid {
        display: flex;
        flex-wrap: wrap;
        width: 342px; 
        height: 342px;
        margin: 0 auto;
        border: 1.5px solid #0f172a;
    }
    .house-box {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        width: 85px;
        height: 85px;
        flex-shrink: 0;
        border: 0.5px solid #0f172a;
    }
    .chart-center {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 170px;
        height: 170px;
        font-weight: 900;
        font-size: 24px;
        border: 0.5px solid #0f172a;
    }
    table { display: flex; flex-direction: column; width: 100%; }
    tr { display: flex; width: 100%; }
    td, th { display: flex; flex: 1; padding: 4px; border: 1px solid #e2e8f0; }
`;
const cssPath = join(__dirname, 'css', 'global.css');
// 1. Read the big Tailwind file
const rawTailwindCss = fs.readFileSync(cssPath, 'utf8');

// 2. SCRUB IT: Remove the things that make Satori/Resvg panic
const sanitizedTailwind = rawTailwindCss
    .replace(/!important/g, '') // Satori hates !important
    .replace(/(z-index|zIndex)\s*:\s*(\d+)px/gi, '$1: $2') // Fix the 50px bug
    .replace(/@media\s+print\s*\{[\s\S]*?\}/g, '') // Remove print styles
    .replace(/column-gap:/g, 'gap:') // Satori prefers 'gap' over specific column-gap
    .replace(/scroll-behavior:\s*smooth/g, ''); // Useless for images


// Pre-clean the CSS string for Satori
const cleanCss = satoriCustomCss.replace(/(z-index|zIndex)\s*:\s*(\d+)px/gi, '$1: $2');

// 3. MERGE: Combine with your specific Astrology chart overrides
const finalSatoriCss = sanitizedTailwind + "\n" + satoriCustomCss;
// Only include the 24KB file if you actually need the utility classes.
// Otherwise, stick to your 'satoriCustomCss' which is 100% Satori-compatible.
// const finalSatoriCss = (useTailwind ? sanitizedTailwind : "") + "\n" + satoriCustomCss;

app.post('/render', async (req, res) => {
    try {
        const fontData = fs.readFileSync('./fonts/arial.ttf');
        const token = req.headers['x-sidecar-token'];
        if (token !== AUTH_TOKEN) return res.status(403).send('Unauthorized');

        const rawHtml = req.body.html;
        
        // 1. Strip and Clean HTML String
        const satoriHtml = prepareNakedHtml(satoriSystemStrip(rawHtml));
        
        // 2. Convert to VNode
        let vNode = toVNode(satoriHtml);
        
        // 3. Deep Clean VNode (The most important step)
        vNode = deepCleanVNode(vNode);

        // 4. Generate SVG
        const svg = await satori(vNode, { 
            width: 1200, 
            height: 1080, 
            fonts: [{ name: 'arial', data: fontData, weight: 400 }],
            css: finalSatoriCss,
        });

        // 5. Render PNG (No manual regex on the SVG string here!)
        const resvg = new Resvg(svg, {
            background: '#ffffff',
            fitTo: { mode: 'width', value: 1200 }
        });
        
        const pngBuffer = resvg.render().asPng();

        res.setHeader('Content-Type', 'image/png');
        res.send(pngBuffer);
    } catch (err) {
        console.error("Render Error:", err.message);
        res.status(500).send(err.message);
    }
});

function deepCleanVNode(node) {
    if (!node || typeof node !== 'object') return node;

    if (Array.isArray(node)) {
        return node.map(deepCleanVNode);
    }

    if (node.props) {
        const s = node.props.style || {};

        // Fix zIndex (Check both casing types)
        ['zIndex', 'z-index'].forEach(key => {
            if (s[key] !== undefined) {
                const val = String(s[key]).replace(/[^0-9-]/g, '');
                s.zIndex = val ? parseInt(val, 10) : 0;
                if (key === 'z-index') delete s[key]; // Normalize to camelCase
            }
        });

        // The "Panic Killer": Ensure no 0-dimensions on flex items
        if (s.display !== 'none') {
            s.display = 'flex';
            if (!s.flexDirection) s.flexDirection = 'column';
            // Resvg crashes on 0 height/width boxes with borders
            if (s.borderWidth || s.border) {
                s.minWidth = s.minWidth || 1;
                s.minHeight = s.minHeight || 1;
            }
        }

        // Nuclear removal of classes to prevent Satori from re-parsing them
        if (node.props.className) delete node.props.className;
        if (node.props.class) delete node.props.class;

        if (node.props.children) {
            node.props.children = deepCleanVNode(node.props.children);
        }
    }
    return node;
}

function satoriSystemStrip(html) {
    return html
        .replace(/<(script|style)\b[^>]*>([\s\S]*?)<\/\1>/gi, '')
        .replace(/!important/g, '')
        .replace(/position:\s*fixed/gi, 'position: absolute');
}

function prepareNakedHtml(html) {
    if (!html) return '';

    return html
        // Catch the 50px unit before it hits the parser
        .replace(/(z-index|zIndex)\s*:\s*(\d+)px/gi, '$1: $2')
        .replace(/(font-weight|fontWeight)\s*:\s*(\d+)px/gi, '$1: $2')
        // Ensure all layout containers are flex
        .replace(/<(div|section|table|tr|td|th)(?![^>]*style=['"])/gi, '<$1 style="display: flex; flex-direction: column;"')
        .replace(/style=(['"])/gi, 'style=$1display: flex; flex-direction: column; ')
        .replace(/\s{2,}/g, ' ')
        .trim();
}

app.listen(3000, '0.0.0.0', () => console.log('Satori Sidecar Ready'));