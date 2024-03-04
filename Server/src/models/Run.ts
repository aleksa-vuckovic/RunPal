import { ObjectId } from 'mongodb'
import mongoose from 'mongoose'

const pathPointSchema = new mongoose.Schema({
    latitude: Number,
    longitude: Number,
    altitude: Number,
    time: Number,
    end: Boolean,
    speed: Number,
    distance: Number,
    kcal: Number
})

const runSchema = new mongoose.Schema({
    id: Number,
    user: String,
    event: {
        type: ObjectId,
        default: null
    },
    room: {
        type: ObjectId,
        default: null
    },
    start: {
        type: Number,
        default: null
    },
    running: {
        type: Number,
        require: true,
        default: 0
    },
    end: {
        type: Number,
        default: null
    },
    paused: {
        type: Boolean,
        required: true,
        default: false
    },
    location: {
        type: pathPointSchema,
        default: null
    },
    path: {
        type: [pathPointSchema],
        default: []
    }
})

export default mongoose.model("runModel", runSchema, "runs")