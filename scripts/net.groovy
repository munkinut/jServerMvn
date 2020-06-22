
name = scriptResource.getName()
i = scriptResource.getIs()
o = scriptResource.getOs()
params = scriptResource.getParams()

scriptResource.write("Connected to " + name + "\r\n")

br = new BufferedReader(new InputStreamReader(i))
bw = new BufferedWriter(new OutputStreamWriter(o))

String line
while ((line = br.readLine()) != null) {
    if (line.equals("QUIT")) {
        bw.writeLine("GOODBYE")
        bw.flush()
        break
    }
    else {
        bw.writeLine(line)
        bw.flush()
    }

}

