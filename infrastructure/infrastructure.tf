variable "organisation" {
  type        = string
  description = "The organisation"
  default     = "IT-DI-IO"
}

provider "aws" {
  version = "~> 2.70"
  region  = "eu-central-1"
}

resource "aws_s3_bucket" "bucket" {
  bucket = "it-di-io-bucket"
  acl    = "public-read"
  tags = {
    "organisation" = var.organisation
  }
}

resource "aws_s3_bucket_policy" "bucket_policy" {
  bucket = aws_s3_bucket.bucket.bucket
  policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Effect" = "Allow"
          "Principal" = {
            "AWS" = aws_iam_role.role.arn
          }
          "Resource" = aws_s3_bucket.bucket.arn
          "Action"   = ["s3:*"]
        },
        {
          "Effect" = "Allow"
          "Principal" = {
            "Service" = "lambda.amazonaws.com"
          }
          "Resource" = aws_s3_bucket.bucket.arn
          "Action"   = ["s3:*"]
        }
      ]
    }
  )
}

resource "aws_s3_bucket_notification" "s3_to_lambda_trigger" {
  bucket = aws_s3_bucket.bucket.bucket
  lambda_function {
    lambda_function_arn = aws_lambda_function.lambda.arn
    events              = ["s3:ObjectCreated:*"]
  }
}

resource "aws_sqs_queue" "queue" {
  name = "it-di-io-queue"
  policy = jsonencode(
    {
      Id = "SQSDefaultPolicy"
      Statement = [
        {
          Action = [
            "sqs:SendMessage",
            "sqs:ReceiveMessage"
          ]
          Effect = "Allow"
          Principal = {
            Service = "s3.amazonaws.com"
          }
        },
      ]
      Version = "2012-10-17"
    }
  )
  tags = {
    "organisation" = var.organisation
  }
}

resource "aws_iam_role" "role" {
  name = "it-di-io-lambda-role"

  assume_role_policy = jsonencode(
    {
      "Version" = "2012-10-17",
      "Statement" = [
        {
          "Action" = "sts:AssumeRole"
          "Principal" = {
            "Service" = "lambda.amazonaws.com"
          }
          "Effect" = "Allow"
        }
      ]
    }
  )

  tags = {
    "organisation" = var.organisation
  }
}

resource "aws_iam_role_policy" "role_policy" {
  role = aws_iam_role.role.name
  policy = jsonencode(
    {
      "Version" : "2012-10-17",
      "Statement" : [
        {
          "Effect"   = "Allow"
          "Resource" = "*"
          "Action"   = ["s3:*"]
        },
        {
          "Effect"   = "Allow"
          "Resource" = "*"
          "Action"   = ["sqs:*"]
        }
      ]
    }
  )
}

resource "aws_lambda_function" "lambda" {
  description      = "Lambda to create messages in SQS queue whenever a file is uploaded to the S3 bucket"
  filename         = "function_payload.zip"
  function_name    = "lambda"
  role             = aws_iam_role.role.arn
  handler          = "lambda.handler"
  source_code_hash = filebase64sha256("function_payload.zip")

  runtime = "python3.7"

  tags = {
    "organisation" = var.organisation
  }
}

resource "aws_lambda_permission" "lambda_permission" {
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.bucket.arn
}
