import mongoose from 'mongoose'

const eventSchema = new mongoose.Schema({
    name: {
        type: String,
        require: true
    },
    description: {
        type: String,
        require: true,
        default: ""
    },
    image: {
        type: String,
        require: true
    },
    time: {
        type: Number,
        require: true
    },
    distance: {
        type: Number,
        require: true
    },
    followers: {
        type: Array<String>,
        require: true,
        default: []
    }
})

export default mongoose.model("eventModel", eventSchema, "events")