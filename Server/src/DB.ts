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
}