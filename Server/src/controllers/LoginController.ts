import express from 'express'
import { DB } from '../DB'
import bcrypt from 'bcrypt'
import { JWT } from '../JWT'
import { Validation } from '../Validations'
import path from 'path'
import fs from 'fs'
import { Utils } from '../Utils'


export class LoginController {

    test = async (req: express.Request, res: express.Response) => {
        let sent = req.query.test
        res.json({message:"ok"})
        //res.status(400).type("text/plain").send("Some plain text.")
    }

    login = async (req: express.Request, res: express.Response) => {
        let user = await DB.user(req.body.email)
        if (user == null) {
            res.json({message: "Email does not exist."})
            return
        }

        if (!(await bcrypt.compare(req.body.password, user.password))) {
            res.json({message: "Incorrect password."})
            return
        }


        let token = JWT.generateJWT(Utils.payload(user))
        res.json({message: "ok", data: token})
    }

    refresh = async(req: express.Request, res: express.Response) => {
        JWT.authenticateJWT(req, res, () => {
            let newToken = JWT.generateJWT(Utils.payload(req.jwt))
            res.json({message: "ok", data: newToken})
        })
    }

    register = async(req: express.Request, res: express.Response) => {
        let input = req.body
        let output: any = {}
        let ret = await Validation.registration(input, output)
        if (ret != "ok") {
            res.json({message: ret})
            return
        }

        let profile: any = req.file
        ret = Validation.profile(profile)
        if (ret != "ok") output.profile = Utils.defaultProfile()
        else {
            let uniqueName = `${Utils.randomUniqueFileName()}.${profile.mimetype.split('/')[1]}`
            let uploadPath = Utils.uploadPath(uniqueName)
            fs.writeFileSync(uploadPath, profile.buffer)
            output.profile = uniqueName
        }

        let salt = await bcrypt.genSalt()
        output.password = await bcrypt.hash(output.password, salt)
        
        ret = await DB.addUser(output)
        if (ret != "ok") {
            res.json({message: "Database error."})
            return
        }

        let token = JWT.generateJWT(Utils.payload(output))
        res.json({message: "ok", data: token})
        return
    }

}