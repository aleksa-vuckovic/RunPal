import mongoose from "mongoose";

let userSchema = new mongoose.Schema({
    email: String,
    password: String,
    name: String,
    last: String,
    profile: String,
    weight: Number
})

export default mongoose.model("userModel", userSchema, "users")