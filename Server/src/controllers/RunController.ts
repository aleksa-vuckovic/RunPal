import express from 'express'
import { Validation } from '../Validations'
import { DB } from '../DB'
import { ObjectId } from 'mongodb'

export class RunController {

    create = async (req: express.Request, res: express.Response) => {
        let input = req.body
        let output: any = {}
        let ret = await Validation.run(input, output, req.jwt)
        if (ret != "ok") {
            res.json({message: ret})
            return
        }
        let existing = await DB.run(output.user, output.id)
        if (existing != null) {
            res.json({message: "ok"})
            return
        }
        ret = await DB.createRun(output)
        res.json({message: ret})
        return
    }

    update = async (req: express.Request, res: express.Response) => {
        let input = req.body
        let output: any = {}
        let ret = Validation.runUpdate(input, output, req.jwt)
        if (ret != "ok") {
            res.json({message: ret})
            return 
        }
        let path = output.path
        delete output.path
        ret = await DB.updateRun(output.user, output.id, output)
        if (ret != "ok") {
            res.json({message: ret})
            return
        }

        if (path.length > 0) ret = await DB.updatePath(output.user, output.id, path)
        res.json({message: ret})
        return
    }

    getUpdate = async (req: express.Request, res: express.Response) => {
        if (!req.query.user) {
            res.json({message: "Not enough data."})
            return
        }
        let match: any = {user: req.query.user}
        if (req.query.id) {
            let id = parseInt(req.query.id as string)
            if (isNaN(id)) {
                res.json({message: "Invalid ID."})
                return
            }
            match.id = id
        }
        else if (req.query.room) match.room = new ObjectId(req.query.room as string)
        else if (req.query.event) match.event = new ObjectId(req.query.event as string)
        else {
            res.json({message: "Not enough data."})
            return
        }
        let since = parseInt(req.query.since as string)
        if (isNaN(since)) since = 0
        let ret = await DB.getRunUpdate(match, since)
        if (ret == null) res.json({message: "Run does not exist."})
        else res.json({message: "ok", data: ret})
        return
    }

    all = async(req: express.Request, res: express.Response) => {
        let until = parseInt(req.query.until as string)
        let limit = parseInt(req.query.limit as string)
        if (isNaN(until) || isNaN(limit)) {
            res.json({message: "Invalid query parameter data."})
            return
        }
        let ret = await DB.getRuns(req.jwt.email, until, limit)
        res.json({message: "ok", data: ret})
    }

    since = async(req: express.Request, res: express.Response) => {
        let since = parseInt(req.query.since as string)
        if (isNaN(since)) {
            res.json({message: "Invalid query parameter data."})
            return
        }
        let ret = await DB.getRunsSince(req.jwt.email, since)
        res.json({message: "ok", data: ret})
        return 
    }

    unfinished = async(req: express.Request, res: express.Response) => {
        let ret = await DB.unfinishedRun(req.jwt.email)
        res.json({message: "ok", data: ret})
        return 
    }

    delete = async(req: express.Request, res: express.Response) => {
        let runId = parseInt(req.params.id)
        if (isNaN(runId)) {
            res.json({message: "Invalid run id."})
            return
        }
        let ret = await DB.deleteRun(req.jwt.email, runId)
        res.json({message: ret})
    }

}