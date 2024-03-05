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
    },
    start: {
        type: Number,
        require: true,
        default: null
    }
})

export default mongoose.model("roomModel", roomSchema, "rooms")