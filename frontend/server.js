const { parse } = require('url');
const next = require('next');
const { createServer } = require('http');
require('dotenv').config({ path: '.env' });
const { ioHttp } = require('./servers/websocket/socketServer');
const dev = process.env.NODE_ENV !== 'production'

const app = next({ dev });

const handle = app.getRequestHandler();


app.prepare().then(() => {
  const httpServer = createServer(async (req, res) => {
    try {
      const parsedUrl = parse(req.url, true);
      await handle(req, res, parsedUrl);
    } catch (err) {
      console.error('Error occurred handling', req.url, err);
      res.statusCode = 500;
      res.end('internal server error');
    }
  });

  const { PORTHTTP, PORTHTTPS } = process.env

  httpServer.listen(PORTHTTP, (err) => {
    if (err) throw err;
    console.log(`Iniciando na porta ${PORTHTTP} (HTTP)`);
  });
  ioHttp.attach(httpServer)

});