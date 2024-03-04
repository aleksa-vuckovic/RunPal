import userModel from './models/User'
import runModel from './models/Run'
import roomModel from './models/Room'
import { ObjectId } from 'mongodb'

export class DB {

    static async user(email: string): Promise<any> {
        let ret = await userModel.findOne({email: email})
        return ret
    }

    static async addUser(user: any): Promise<string> {
        let ret = await userModel.insertMany([user])
        return "ok"
    }
    static async updateUser(email: string, data: any) {
        let ret = await userModel.updateOne({email: email}, {$set: data})
        if (ret.modifiedCount > 0) return "ok"
        else return "Database error."
    }
    static async run(user: string, id: number): Promise<any> {
        //TO DO -> return the corresponding run object NO path
    }

    static async createRun(run: any): Promise<string> {
        let ret = await runModel.insertMany([run])
        return "ok"
    }
    static async updateRun(user: string, id: number, data: any): Promise<string> {
        let ret = await runModel.updateOne({user: user, id: id}, {$set: data})
        if (ret.modifiedCount > 0) return "ok"
        else return "Database error."
    }
    static async updatePath(user: string, id: number, path: any): Promise<string> {
        let ret = await runModel.updateOne({user: user, id: id}, {$push: {path: {$each: path}}})
        if (ret.modifiedCount > 0) return "ok"
        else return "Database error."
    }
    static async getRunUpdate(match: any, since: number): Promise<any> {
        let ret = await runModel.aggregate([
            {
                $match: match
            },
            {
                $project: {
                    _id: 0,
                    run: {
                        id: "$id",
                        user: "$user",
                        room: "$room",
                        event: "$event",
                        start: "$start",
                        running: "$running",
                        end: "$end"
                    },
                    location: "$location",
                    path: {
                        $filter: {
                            input: "$path",
                            cond: {$gt: ["$$this.time", since]}
                        }
                    }
                }
            }
        ])
        if (ret.length > 0) return ret[0]
        else return null
    }

    static async createRoom(): Promise<string> {
        let room = new roomModel({
            members: [],
            ready: []
        })
        let saved = await room.save()
        return saved._id.toString()
    }

    static async joinRoom(user: string, room: string): Promise<string> {
        let ret = await roomModel.updateOne({_id: new ObjectId(room)}, {$addToSet: {members: user}})
        if (ret.matchedCount > 0) return "ok"
        else return "Room does not exist."
    }

    static async readyRoom(user: string, room: string): Promise<string> {
        let ret = await roomModel.updateOne({_id: new ObjectId(room), members: user}, {$addToSet: {ready: user}})
        if (ret.matchedCount > 0) return "ok"
        else return "Room does not exist, or the user is not a member."
    }

    static async leaveRoom(user: string, room: string): Promise<string> {
        let ret = await roomModel.updateOne({_id: room}, {$pull: {members: user, ready: user}})
        if (ret.matchedCount > 0) return "ok"
        else return "Room does not exist."
    }

    static async room(id: string): Promise<any> {
        return await roomModel.findOne({_id: new ObjectId(id)})
    }
}