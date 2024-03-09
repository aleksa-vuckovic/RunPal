import { ObjectId } from 'mongodb'
import mongoose from 'mongoose'

const pathPointSchema = new mongoose.Schema({
    latitude: {
        type: Number,
        default: 0,
        required: true
    },
    longitude: {
        type: Number,
        default: 0,
        required: true
    },
    altitude: {
        type: Number,
        default: 0,
        required: true
    },
    time: {
        type: Number,
        default: 0,
        required: true
    },
    end: {
        type: Boolean,
        default: false,
        required: true
    },
    speed: {
        type: Number,
        default: 0,
        required: true
    },
    distance: {
        type: Number,
        default: 0,
        required: true
    },
    kcal: {
        type: Number,
        default: 0,
        required: true
    },
}, {
    _id: false
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
        required: true,
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
        required: true,
        default: () => ({})
    },
    path: {
        type: [pathPointSchema],
        default: []
    }
})

export default mongoose.model("runModel", runSchema, "runs")