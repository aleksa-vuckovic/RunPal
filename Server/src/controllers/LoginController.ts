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
        res.json({message: "You sent "+ sent})
        //res.status(400).type("text/plain").send("Some plain text.")
    }

    login = async (req: express.Request, res: express.Response) => {
        let user = await DB.user(req.body.email)
        if (user == null) {
            res.status(401).type("text/plain").send("Email does not exist.")
            return
        }

        if (!(await bcrypt.compare(req.body.password, user.password))) {
            res.status(401).type("text/plain").send("Incorrect password.")
            return
        }

        delete user.password
        delete user.profile
        let token = JWT.generateJWT(user)
        res.status(200).type("text/plain").send(token)
    }

    refresh = async(req: express.Request, res: express.Response) => {
        JWT.authenticateJWT(req, res, () => {
            let newToken = JWT.generateJWT(req.jwt)
            res.status(200).type("text/plain").send(newToken)
        })
    }

    register = async(req: express.Request, res: express.Response) => {
        let input = req.body
        let output: any = {}
        let ret = await Validation.registration(input, output)
        if (ret != "ok") {
            res.status(400).type("text/plain").send(ret)
            return
        }

        let profile: any = req.file
        ret = Validation.profile(profile)
        if (ret != "ok") output.profile = "default.png"
        else {
            let uniqueName = `${Date.now()}.${profile.mimetype.split('/')[1]}`
            let uploadPath = Utils.uploadPath(uniqueName)
            fs.writeFileSync(uploadPath, profile.buffer)
            output.profile = uniqueName
        }

        let salt = await bcrypt.genSalt()
        output.password = await bcrypt.hash(output.password, salt)
        
        ret = await DB.addUser(output)
        if (ret != "ok") {
            res.status(500).type("text/plain").send("Database error.")
            return
        }

        delete output.profile
        delete output.password
        let token = JWT.generateJWT(output)
        res.status(200).type("text/plain").send(token)
        return
    }

}