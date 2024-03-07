import express from 'express'
import { Validation } from '../Validations'
import { Utils } from '../Utils'
import fs from 'fs'
import { DB } from '../DB'

export class EventController {

    create = async(req: express.Request, res: express.Response) => {
        let input = req.body
        let output: any = {}
        let ret = Validation.event(input, output, req.jwt)
        if (ret != "ok") {
            res.json({message: ret})
            return
        }

        let image: any = req.file
        ret = Validation.profile(image)
        if (ret != "ok") output.image = Utils.defaultEventImage()
        else {
            let uniqueName = `${Utils.randomUniqueFileName()}.${image.mimetype.split('/')[1]}`
            let uploadPath = Utils.uploadPath(uniqueName)
            fs.writeFileSync(uploadPath, image.buffer)
            output.image = uniqueName
        }
        
        ret = await DB.createEvent(output)
        res.json({message: "ok", data: ret})
        return

    }
    data = async(req: express.Request, res: express.Response) => {
        let ret = await DB.event(req.params.event as string, req.jwt.email)
        if (ret == null) res.json({message: "Event not found."})
        else res.json({message: "ok", data: ret})
        return
    }
    find = async(req: express.Request, res: express.Response) => {
        let following = Utils.parseBoolean(req.query.following)
        let search = req.query.search ? req.query.search as string : null
        let ret = await DB.findEvent(req.jwt.email, search, following)
        res.json({message: "ok", data: ret})
    }
    follow = async(req: express.Request, res: express.Response) => {
        let ret =await DB.followEvent(req.jwt.email, req.params.event)
        res.json({message: ret})
    }
    unfollow = async(req: express.Request, res: express.Response) => {
        let ret =await DB.unfollowEvent(req.jwt.email, req.params.event)
        res.json({message: ret})
    }
}