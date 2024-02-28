import express from 'express'
import { LoginController } from '../controllers/LoginController'

const loginRouter = express.Router()

loginRouter.route("/test").get((req: express.Request, res: express.Response) => {
    new LoginController().test(req, res)
})

export default loginRouter