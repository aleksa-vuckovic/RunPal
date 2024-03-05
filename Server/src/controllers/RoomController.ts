import express from 'express'
import { DB } from '../DB'

export class RoomController {

    create =  async (req: express.Request, res: express.Response) => {
        let user = req.jwt.email
        let room = await DB.createRoom()
        let ret = await DB.joinRoom(user, room)
        if (ret != "ok") res.json({message: ret})
        else res.json({message: "ok", data: room})
    }

    join = async (req: express.Request, res: express.Response) => {
        let user = req.jwt.email
        let room = req.params.room
        let state = await DB.room(room)
        if (state.start != null) {
            res.json({message: "The run has already started."})
            return
        }
        else if (state.members.length >= 5) {
            res.json({message: "The room is full (5 users max)."})
            return
        }
        let ret = await DB.joinRoom(user, room)
        res.json({message: ret})
    }

    ready = async (req: express.Request, res: express.Response) => {
        let user = req.jwt.email
        let room = req.params.room
        let ret = await DB.readyRoom(user, room)
        let status = await DB.room(room)
        if (status != null && status.members.length == status.ready.length && status.start == null) {
            await DB.startRoom(room)
        }
        res.json({message: ret})
    }

    leave = async (req: express.Request, res: express.Response) => {
        let user = req.jwt.email
        let room = req.params.room
        let state = await DB.room(room)
        if (state == null) {
            res.json({message: "Room does not exist."})
            return
        }
        if (state.ready.includes(user)) {
            res.json({message: "Cannot leave when ready."})
            return
        }
        let ret = await DB.leaveRoom(user, room)
        res.json({message: ret})
        return
    }

    status = async (req: express.Request, res: express.Response) => {
        let room = req.params.room
        let ret = await DB.room(room)
        if (ret == null) res.json({message: "Room does not exist"})
        else res.json({message: "ok", data: ret})
        return
    }
}