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
        background-color: #ffffff;
    }
    /* Force specific chart dimensions in CSS as a backup */
    .chart-grid {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
        width: 340px; 
        height: 340px;
        border: 2px solid #000;
    }
    .house-box {
        display: flex;
        flex-direction: column;
        width: 85px;
        height: 85px;
        flex-shrink: 0;
        border: 1px solid #333;
        align-items: center;
        justify-content: center;
    }
    /* Force tables to behave like flex rows */
    table { display: flex; flex-direction: column; width: 100%; }
    tr { display: flex; flex-direction: row; width: 100%; }
    td, th { display: flex; flex: 1; padding: 4px; }
`;

const cssPath = join(__dirname, 'css', 'global.css');
const rawTailwindCss = fs.readFileSync(cssPath, 'utf8');

const sanitizedTailwind = rawTailwindCss
    .replace(/!important/g, '')
    .replace(/(z-index|zIndex)\s*:\s*(\d+)px/gi, '$1: $2')
    .replace(/@media\s+print\s*\{[\s\S]*?\}/g, '')
    .replace(/column-gap:/g, 'gap:')
    .replace(/scroll-behavior:\s*smooth/g, '');

const finalSatoriCss = sanitizedTailwind + "\n" + satoriCustomCss;

app.post('/render', async (req, res) => {
    try {
        const fontData = fs.readFileSync('./fonts/arial.ttf');
        const token = req.headers['x-sidecar-token'];
        if (token !== AUTH_TOKEN) return res.status(403).send('Unauthorized');

        let rawHtml = req.body.html;
        
        // 1. Pre-process string
        const satoriHtml = prepareNakedHtml(satoriSystemStrip(rawHtml));
        
        // 2. Build VNode
        let vNode = toVNode(satoriHtml);
        
        // 3. Deep Clean
        vNode = deepCleanVNode(vNode);

        // 4. Satori Render
        const svg = await satori(vNode, { 
            width: 1200, 
            height: 1600, // Increased height to ensure charts aren't cut off
            fonts: [{ name: 'arial', data: fontData, weight: 400 }],
            css: satoriCustomCss,
        });

        const resvg = new Resvg(svg, {
            background: '#ffffff',
            fitTo: { mode: 'width', value: 1200 }
        });
        
        res.setHeader('Content-Type', 'image/png');
        res.send(resvg.render().asPng());
    } catch (err) {
        console.error("Render Error:", err.message);
        res.status(500).send(err.message);
    }
});

function deepCleanVNode(node) {
    if (!node || typeof node !== 'object') return node;
    if (Array.isArray(node)) return node.map(deepCleanVNode);

    if (node.props) {
        node.props.style = node.props.style || {};
        const s = node.props.style;
        const classes = String(node.props.className || node.props.class || "");

        // --- STABILIZATION BLOCK: This stops the resvg panic ---
        const numericProps = ['width', 'height', 'fontSize', 'lineHeight', 'borderWidth', 'gap'];
        numericProps.forEach(prop => {
            if (s[prop] !== undefined) {
                // Strip everything except numbers and decimals
                const val = parseFloat(String(s[prop]).replace(/[^-0-9.]/g, ''));
                if (!isNaN(val) && val > 0) {
                    s[prop] = val; // Force to raw NUMBER (no "px")
                } else if (typeof s[prop] === 'string' && s[prop].includes('%')) {
                    // Percentages are okay as strings
                } else {
                    delete s[prop]; // Remove 0, NaN, or invalid strings to prevent crash
                }
            }
        });

        // --- LAYOUT ENFORCEMENT ---
        s.display = 'flex'; // Satori requirement

        if (classes.includes('chart-grid')) {
            s.width = 340;
            s.height = 340;
            s.flexDirection = 'row';
            s.flexWrap = 'wrap';
            s.alignContent = 'flex-start'; 
        }

        if (classes.includes('house-box')) {
            s.width = 85;
            s.height = 85;
            s.flexShrink = 0; // Ensures the square doesn't collapse to 0 width
            s.flexDirection = 'column';
            s.alignItems = 'center';
            s.justifyContent = 'center';
        }

        // Table Row Horizontal Fix
        if (node.type === 'tr' || classes.includes('flex-row')) {
            s.flexDirection = 'row';
        }

        if (node.props.children) {
            node.props.children = deepCleanVNode(node.props.children);
        }
    }
    return node;
}

function satoriSystemStrip(html) {
    if (!html) return '';
    return html
        .replace(/<(script|style)\b[^>]*>([\s\S]*?)<\/\1>/gi, '')
        // Clean loading artifacts seen in image_978361.png and others
        .replace(/Emailing Your horoscope\.\.\./gi, '')
        .replace(/,\s*Please wait\./gi, '')
        .replace(/Sending Email/gi, '')
        .replace(/Print \/ Export Image/gi, '')
        .replace(/Done/gi, '')
        .replace(/Chat to get predictions.*/gi, '')
        .replace(/!important/g, '');
}

function prepareNakedHtml(html) {
    if (!html) return '';
    return html
        // Remove empty containers that might have borders but no size
        .replace(/<div[^>]*>\s*<\/div>/gi, '')
        // Bind symbols to prevent line breaks in geometry
        .replace(/(\d+)\s*°/g, '$1°') 
        .replace(/(\d+)\s*'/g, "$1'")
        // Standardize z-index
        .replace(/(z-index|zIndex)\s*:\s*(\d+)px/gi, '$1: $2')
        .replace(/\s{2,}/g, ' ')
        .trim();
}

app.listen(3000, '0.0.0.0', () => console.log('Satori Sidecar Ready'));