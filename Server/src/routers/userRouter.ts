import express from 'express'
import multer from 'multer'
import { JWT } from '../JWT'
import { UserController } from '../controllers/UserController'

const upload = multer({
    storage: multer.memoryStorage(),
    limits: {
        fileSize: 5*1024*1024,
        files: 1
    }
});

const userRouter = express.Router()

userRouter.use(JWT.authenticateJWT)

userRouter.route("/data").get((req: express.Request, res: express.Response) => {
    new UserController().data(req, res)
})
userRouter.route("/update").post(upload.single('profile'), (req: express.Request, res: express.Response) => {
    new UserController().update(req, res)
})

export default userRouter