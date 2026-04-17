import { html as toVNode } from 'satori-html';

const express = require('express');
const satori = require('satori').default;
const { Resvg } = require('@resvg/resvg-js');
const fs = require('fs');

const app = express();
app.use(express.json({ limit: '5mb' }));

// let SIDECAR_TOKEN = "63a37ad3b434ef125e6f2daa80876f0d5f657e65707518b4729ebeb33cc62221";
// 1. SECURE TOKEN (Set this in Render Env Vars)
const AUTH_TOKEN = process.env.SIDECAR_TOKEN || 'default-local-token';

// Load your font (Tailwind needs a font to calculate layout)
const fontData = fs.readFileSync('./fonts/arial.ttf');

app.post('/render', async (req, res) => {
    // 2. AUTH CHECK
    const token = req.headers['x-sidecar-token'];
    if (token !== AUTH_TOKEN) return res.status(403).send('Unauthorized');

    try {
        const { html } = req.body;

        const rawHtml = req.body.html;
        const vNode = toVNode(rawHtml);

        const svg = await satori(vNode, {
            width: 1200,
            height: 1080,
            background: '#ffffff',
            fonts: [{ name: 'arial', data: fontData, weight: 400 }]
        });

        const resvg = new Resvg(svg);
        const pngData = resvg.render();
        const pngBuffer = pngData.asPng();
        // console.log("PNG Generated. Length:", pngBuffer.length);

        res.setHeader('Content-Type', 'image/png');
        res.set('Content-Length', pngBuffer.length);
        res.send(pngBuffer);
    } catch (err) {
        res.status(500).send(err.message);
    }
});

app.listen(3000, '0.0.0.0', () => console.log('Satori Sidecar on port 3000'));