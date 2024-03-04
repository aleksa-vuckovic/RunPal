import mongoose from 'mongoose'

const roomSchema = new mongoose.Schema({
    members: {
        type: Array<String>,
        require: true,
        default: []
    },
    ready: {
        type: Array<String>,
        require: true,
        default: []
    }
})

export default mongoose.model("roomModel", roomSchema, "rooms")