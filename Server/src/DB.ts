import userModel from './models/User'
import runModel from './models/Run'

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
}