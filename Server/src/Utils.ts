import path from "path";


export class Utils {

    static defaultProfile(): string {
        return "default.png"
    }
    static defaultEventImage(): string {
        return "default2.png"
    }

    static uploadDir(): string {
        return path.join(__dirname, "..", "uploads")
    }
    static uploadPath(filename: string) {
        return path.join(this.uploadDir(), filename)
    }

    static payload(user: any): any {
        return {
            email: user.email,
            name: user.name,
            last: user.last,
            profile: user.profile
        }
    }
    static randomUniqueFileName(): string {
        return `${Date.now()}`
    }
    static parseBoolean(input: any): boolean | null {
        if (input === false || input === "false") return false
        else if (input) return true
        else return null
    }
}