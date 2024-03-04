import { ObjectId } from "mongodb"
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
    static async run(input: any, output: any, jwt: any): Promise<string> {
        if (!input || !input.user || !input.id) return "Not enough data."
        if (jwt.email != input.user) return "Cannot create a run for someone else."
        output.user = input.user
        output.id = parseInt(input.id)
        if (isNaN(output.id)) return "Run id must be an integer."
        let run = await (DB.run(output.user, output.id))
        if (run != null) return "Run already exists."
        
        if (input.start) {
            output.start = parseInt(input.start)
            if (isNaN(output.start)) return "Start time must be an integer."
        } else output.start = null
        if (input.running) {
            output.running = parseInt(input.running)
            if (isNaN(output.running)) return "Running time must be an integer."
        } else output.running = 0
        if (input.end) {
            output.end = parseInt(input.end)
            if (isNaN(output.end)) return "End time must be an integer."
        } else output.end = null
        if (input.paused && input.paused != "false") output.paused = true
        else output.paused = false 
       
        if (input.event) {
            //TO DO check that the event exists
            output.event = new ObjectId(input.event)
        } else output.event = null
        if (input.room) {
            //TO DO check that the user joined the room
            output.room = new ObjectId(input.room)
        }

        output.location = null
        output.path = []

        return "ok"
    }

    private static runLocation(input: any, output: any): string {
        if (!input) return "Not enough data."
        output.latitude = parseFloat(input.latitude)
        output.longitude = parseFloat(input.longitude)
        output.altitude = parseFloat(input.altitude)
        output.time = parseInt(input.time)
        output.end = (input.end  && input.end != "false") ? true : false
        output.speed = parseFloat(input.speed)
        output.distance = parseFloat(input.distance)
        output.kcal = parseFloat(input.kcal)

        if (isNaN(output.latitude) || isNaN(output.longitude)
            || isNaN(output.altitude) || isNaN(output.time)
            || isNaN(output.speed) || isNaN(output.distance)
            || isNaN(output.kcal)) return "Incorrect number format."
        return "ok"
    }

    static runUpdate(input: any, output: any, jwt: any): string {
        if (!input) return "Not enough data."
        let run = input.run
        let location = input.location
        let path = input.path

        if (!run.user || !run.id) return "Not enough data."
        if (jwt.email != run.user) return "Cannot update a run for someone else."
        output.user = run.user
        output.id = parseInt(run.id)
        if (isNaN(output.id)) return "Run id must be an integer."
        if (run.start) {
            output.start = parseInt(run.start)
            if (isNaN(output.start)) return "Start time must be an integer."
        }
        if (run.running) {
            output.running = parseInt(run.running)
            if (isNaN(output.running)) return "Running time must be an integer."
        }
        if (run.end) {
            output.end = parseInt(run.end)
            if (isNaN(output.end)) return "End time must be an integer."
        }
        if (run.paused === true || run.paused === "true") output.paused = true
        else if (run.paused === false || run.paused === "false") output.paused = false
        

        if (location) {
            let validatedLocation = {}
            let ret = Validation.runLocation(location, validatedLocation)
            if (ret != "ok") return ret
            output.location = validatedLocation
        }

        if (path) {
            let validatedPath: any[] = []
            try {
                path.forEach((pathPoint: any) => {
                    let validatedPoint = {}
                    let ret = Validation.runLocation(pathPoint, validatedPoint)
                    if (ret != "ok") return ret
                    validatedPath.push(validatedPoint)
                });
            } catch(e) { return "Path must be an array." }
            output.path = validatedPath
        } else output.path = []
        return "ok"
    }
}