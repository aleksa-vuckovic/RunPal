import path from "path";


export class Utils {

    static uploadPath(filename: string) {
        return path.join(__dirname, "..", "uploads", filename)
    }
}