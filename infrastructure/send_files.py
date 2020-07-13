import boto3

s3 = boto3.resource('s3')
for i in range(10):
    s3.Bucket('it-di-io-bucket').upload_file('lambda.py', f'lambda-{i}.py')
