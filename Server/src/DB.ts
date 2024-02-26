import userModel from './models/User'

export class DB {

    static async user(name: String): Promise<string> {
        let ret = await userModel.findOne({name: name})
        return ret?.last ?? "not found"
    }
}