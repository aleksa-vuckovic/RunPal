import express from 'express'
import { JWT } from '../JWT'
import { RoomController } from '../controllers/RoomController'
import Room from '../models/Room'

const roomRouter = express.Router()

roomRouter.use(JWT.authenticateJWT)

roomRouter.route("/create").get(async (req: express.Request, res: express.Response) => {
    new RoomController().create(req, res)
})
roomRouter.route("/join/:room").get(async (req: express.Request, res: express.Response) => {
    new RoomController().join(req, res)
})
roomRouter.route("/ready/:room").get(async (req: express.Request, res: express.Response) => {
    new RoomController().ready(req, res)
})
roomRouter.route("/leave/:room").get(async (req: express.Request, res: express.Response) => {
    new RoomController().leave(req, res)
})
roomRouter.route("/status/:room").get(async (req: express.Request, res: express.Response) => {
    new RoomController().status(req, res)
})

export default roomRouter