import zipfile

with zipfile.ZipFile("function_payload.zip", "w") as zip:
    zip.write("lambda.py")
