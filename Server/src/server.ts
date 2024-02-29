import express from 'express'
import mongoose from 'mongoose'
import loginRouter from './routers/loginRouter'

mongoose.connect("mongodb+srv://aleksavuckovic77:nintendowii@iepproba.yn7bmhq.mongodb.net/runpal")
mongoose.connection.once('open', () => {
    console.log("db ok")
})


const app = express()
app.use(express.json())
app.use(express.urlencoded({extended: true}))


app.use("/", loginRouter)
const port = process.env.PORT || 4000;
app.listen(port, () => console.log("Express server running on " + port + "."))