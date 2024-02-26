import mongoose from "mongoose";

let userSchema = new mongoose.Schema({
    name: String,
    last: String
})

export default mongoose.model("userModel", userSchema, "users")