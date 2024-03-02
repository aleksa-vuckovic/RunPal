import userModel from './models/User'

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
}