import express from 'express'
import mongoose from 'mongoose'
import { DB } from './DB'

mongoose.connect("mongodb+srv://aleksavuckovic77:nintendowii@iepproba.yn7bmhq.mongodb.net/runpal")
mongoose.connection.once('open', () => {
    console.log("db ok")
})


const app = express()
app.use(express.json())


app.get("/", async (req: express.Request, res: express.Response) => {
    let last = await DB.user("Aleksa")
    res.json({message: "Aleksa's last name is " + last})
})


const port = process.env.PORT || 4000;
app.listen(port, () => console.log("Express server running on " + port + "."))