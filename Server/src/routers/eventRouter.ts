import express from 'express'
import { EventController } from '../controllers/EventController'
import { JWT } from '../JWT'
import multer from 'multer';

const upload = multer({
    storage: multer.memoryStorage(),
    limits: {
        fileSize: 2*1024*1024,
        files: 1
    }
});

const eventRouter = express.Router()

eventRouter.use(JWT.authenticateJWT)

eventRouter.route("/create").post(upload.single("image"), async (req: express.Request, res: express.Response) => {
    new EventController().create(req, res)
})
eventRouter.route("/data/:event").get(async (req: express.Request, res: express.Response) => {
    new EventController().data(req, res)
})
eventRouter.route("/find").get(async (req: express.Request, res: express.Response) => {
    new EventController().find(req, res)
})
eventRouter.route("/follow/:event").get(async (req: express.Request, res: express.Response) => {
    new EventController().follow(req, res)
})
eventRouter.route("/unfollow/:event").get(async (req: express.Request, res: express.Response) => {
    new EventController().unfollow(req, res)
})
eventRouter.route("/ranking/:event").get(async (req: express.Request, res: express.Response) => {
    new EventController().ranking(req, res)
})
eventRouter.route("/rankinglive/:event").get(async (req: express.Request, res: express.Response) => {
    new EventController().rankingLive(req, res)
})

export default eventRouter