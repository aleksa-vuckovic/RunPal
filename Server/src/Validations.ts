import { DB } from "./DB"


export class Validation {


    static async registration(input: any, output: any): Promise<string> {
        if (!input || !input.email || !input.password || !input.name || !input.last || !input.weight) return "Not enough data."
        let existingUser = await DB.user(input.email)
        if (existingUser != null) return "The email is already in use."
        let weight = parseFloat(input.weight)
        console.log(input)
        console.log("input.weight = " + input.weight + " weight = " + weight)
        if (isNaN(weight)) return "Weight must be a number."

        output.email = input.email
        output.password = input.password
        output.name = input.name
        output.last = input.last
        output.weight = input.weight
        return "ok"
    }
    static profile(file: any): string {
        if (!file) return "No file."
        let type = file.mimetype
        if (type != "image/png") return "Unsupported image type."
        return "ok"
    }
    static userUpdate(input: any, output: any): string {
        if (input.name) output.name = input.name
        if (input.last) output.last = input.last
        if (input.weight) {
            let weight = parseFloat(input.weight)
            if (isNaN(weight)) return "Weight must be a number."
            output.weight = weight
        }
        return "ok"
    }
}