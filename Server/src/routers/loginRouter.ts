import express from 'express'
import { LoginController } from '../controllers/LoginController'
import multer from 'multer';

const upload = multer({
    storage: multer.memoryStorage(),
    limits: {
        fileSize: 5*1024*1024,
        files: 2
    }
});

const loginRouter = express.Router()

loginRouter.route("/test").get((req: express.Request, res: express.Response) => {
    new LoginController().test(req, res)
})
loginRouter.route("/login").post((req: express.Request, res: express.Response) => {
    new LoginController().login(req, res)
})
loginRouter.route("/refresh").get((req: express.Request, res: express.Response) => {
    new LoginController().login(req, res)
})
loginRouter.route("/register").post(upload.single("profile"), (req, res) => {
    new LoginController().register(req, res)
})


export default loginRouter