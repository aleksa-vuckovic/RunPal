import express from 'express'
import mongoose from 'mongoose'
import loginRouter from './routers/loginRouter'
import userRouter from './routers/userRouter'
import path from 'path'

mongoose.connect("mongodb+srv://aleksavuckovic77:nintendowii@iepproba.yn7bmhq.mongodb.net/runpal")
mongoose.connection.once('open', () => {
    console.log("db ok")
})


const app = express()
app.use(express.json())
app.use(express.urlencoded({extended: true}))


app.use("/", loginRouter)
app.use("/user", userRouter)
app.use('/uploads', express.static(path.join(__dirname, "..", "uploads")));
const port = process.env.PORT || 4000;
app.listen(port, () => console.log("Express server running on " + port + "."))