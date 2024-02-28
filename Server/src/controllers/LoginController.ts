import express from 'express'


export class LoginController {

    test = async (req: express.Request, res: express.Response) => {
        let sent = req.query.test
        res.json({message: "You sent "+ sent})
    }
}