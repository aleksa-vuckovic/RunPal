import express from 'express'
import fs from 'fs'
import { DB } from '../DB'
import { Utils } from '../Utils'
import { Validation } from '../Validations'


export class UserController {

    data = async (req: express.Request, res: express.Response) => {
        let email = req.query.email
        if (!email || typeof email != 'string') {
            res.json({message: "Invalid request."})
            return
        }

        let user = await DB.user(email)
        if (user == null) {
            res.json({message: "User does not exist."})
            return
        }

        /* DOES NOT WORK?
        delete user._id
        delete user.password
        */
        user = {
            email: user.email,
            name: user.name,
            last: user.last,
            weight: user.weight,
            profile: user.profile
        }
        res.json({message: "ok", data: user})
        return
    }

    update = async (req: express.Request, res: express.Response) => {
        let input = req.body
        let output: any = {}
        let ret = Validation.userUpdate(input, output)
        if (ret != "ok") {
            res.json({message: ret})
            return
        }

        let profile: any = req.file
        ret = Validation.profile(profile)
        if (ret == "ok") {
            let previous = req.jwt.profile //delete previous profile picture
            if (previous != Utils.defaultProfile()) fs.unlinkSync(Utils.uploadPath(previous))
            
            let uniqueName = `${Utils.randomUniqueFileName()}.${profile.mimetype.split('/')[1]}`
            let uploadPath = Utils.uploadPath(uniqueName)
            fs.writeFileSync(uploadPath, profile.buffer)
            output.profile = uniqueName
        }
        
        ret = await DB.updateUser(req.jwt.email, output)
        res.json({message: ret})
        return
    }
}