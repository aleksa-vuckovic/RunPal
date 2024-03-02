import jwt from 'jsonwebtoken'
import express from 'express'

declare global {
    namespace Express {
        interface Request {
            jwt?: any
        }
    }
}

var secret: string = "psychic life of power"

export class JWT {
    

    static generateJWT(payload: any): string {
        return jwt.sign(payload, secret, {expiresIn: "3d"})
    }

    static authenticateJWT(req: express.Request, res: express.Response, next: express.NextFunction) {
        const auth = req.headers["authorization"]
        if (!auth) res.status(401).type("text/plain").send("Failed to authenticate.")
        else {
            let token = auth.split(" ")[1]
            jwt.verify(token, secret, (err, decoded) => {
                if (err) res.status(401).type("text/plain").send(err.message)
                else {
                    req.jwt = decoded
                    next()
                }
            })
        }
    }

}







