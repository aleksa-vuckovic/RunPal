import express from 'express'
import { JWT } from '../JWT'
import { RunController } from '../controllers/RunController'

const runRouter = express.Router()

runRouter.use(JWT.authenticateJWT)

runRouter.route("/create").post(async (req: express.Request, res: express.Response) => {
    new RunController().create(req, res)
})
runRouter.route("/update").post(async (req: express.Request, res: express.Response) => {
    new RunController().update(req, res)
})
runRouter.route("/getupdate").get(async (req: express.Request, res: express.Response) => {
    new RunController().getUpdate(req, res)
})
runRouter.route("/all").get(async (req: express.Request, res: express.Response) => {
    new RunController().all(req, res)
})
runRouter.route("/since").get(async (req: express.Request, res: express.Response) => {
    new RunController().since(req, res)
})
runRouter.route("/unfinished").get(async (req: express.Request, res: express.Response) => {
    new RunController().unfinished(req, res)
})
runRouter.route("/delete/:id").get(async (req: express.Request, res: express.Response) => {
    new RunController().delete(req, res)
})

export default runRouter