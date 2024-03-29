provider "aws" {
  region     = "ap-south-1"
}

#S3 bucket creation
resource "aws_s3_bucket" "my-first-gajendran-r-terraform-s3-bucket" {
  bucket = "bits-wilp-ap-south-1"
  #region="ap-south-1" 
  tags = {
    Name  = "bits-wilp-ap-south-1"
    Owner = "202117BH009@wilp.bits-pilani.ac.in"
  }
  lifecycle {
    prevent_destroy = true
  }
}
#Store the terraform state file in S3
terraform {
  backend "s3" {
    bucket     = "bits-wilp-terraformstat-ap-south-1"
    key        = "terraform.tfstate"
    region     = "ap-south-1"
    access_key = "AKIATWVMFH3A36SY6FTD"
    secret_key = "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"
    # profile = "terraform-user"
  }
}
#Lambda function
resource "aws_lambda_function" "bits-wilp-URLValidatorElite" {
  role          = aws_iam_role.bits-wilp-URLValidationElite-lambda_execution_role.arn
  function_name = "bits-wilp-URLValidationElite"
  runtime       = "java8"
  handler       = "com.wilp.bits.lambda.ConnectEC2UsingSSM"
  memory_size   = 512
  timeout       = 900
  s3_bucket     = "my-bits-wilp-jars"
  s3_key        = "URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
  # filename      = "my-bits-wilp-jars/URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
  # source_code_hash = filebase64sha256("s3://my-bits-wilp-jars/URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar/URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar")

  lifecycle {
    prevent_destroy = true
  }
}
data "aws_iam_policy" "s3_read_policy" {
  arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

#IAM role for Lambda
resource "aws_iam_role" "bits-wilp-URLValidationElite-lambda_execution_role" {
  name               = "bits-wilp-URLValidationElite-lambda_execution_role"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": [
          "lambda.amazonaws.com"
        ]
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
  lifecycle {
    ignore_changes = [
      assume_role_policy
    ]
  }
}

#Attach Policies to the Lambda execution role
resource "aws_iam_role_policy_attachment" "bits-wilp-URLValidationElite-lambda_execution_role_attachement" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  role       = aws_iam_role.bits-wilp-URLValidationElite-lambda_execution_role.name
}
resource "aws_iam_role_policy_attachment" "lambda_s3_read_policy_attachment" {
  policy_arn = data.aws_iam_policy.s3_read_policy.arn
  role       = aws_iam_role.bits-wilp-URLValidationElite-lambda_execution_role.name
}

#Create a EC2 instance
resource "aws_instance" "Bits_wilp_DP" {
  ami                  = "ami-0a0f1259dd1c90938"
  instance_type        = "t2.micro"
  key_name             = aws_key_pair.my-bits-wilp-aws-key-pair.key_name
  iam_instance_profile = aws_iam_role.bits-wilp-URLValidationElite-ec2_execution.name
  tags = {
    Name = "Bits_wilp_DP"
  }
}
output "Bits_wilp_DP_id" {
  value = aws_instance.Bits_wilp_DP.id
}
#IAM role for EC2-SSM role
resource "aws_iam_role" "bits-wilp-URLValidationElite-ec2_execution" {
  name               = "bits-wilp-instance-ssm-role"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ssm:DescribeAssociation",
        "ssm:GetDeployablePatchSnapshotForInstance",
        "ssm:GetDocument",
        "ssm:DescribeDocument",
        "ssm:GetManifest",
        "ssm:GetParameter",
        "ssm:GetParameters",
        "ssm:ListAssociations",
        "ssm:ListInstanceAssociations",
        "ssm:PutInventory",
        "ssm:PutComplianceItems",
        "ssm:PutConfigurePackageResult",
        "ssm:UpdateAssociationStatus",
        "ssm:UpdateInstanceAssociationStatus",
        "ssm:UpdateInstanceInformation"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ssmmessages:CreateControlChannel",
        "ssmmessages:CreateDataChannel",
        "ssmmessages:OpenControlChannel",
        "ssmmessages:OpenDataChannel"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ec2messages:AcknowledgeMessage",
        "ec2messages:DeleteMessage",
        "ec2messages:FailMessage",
        "ec2messages:GetEndpoint",
        "ec2messages:GetMessages",
        "ec2messages:SendReply"
      ],
      "Resource": "*"
    }
  ]
}
EOF

  lifecycle {
    ignore_changes = [
      assume_role_policy
    ]
  }
}
# Attach SSM role to EC2 instance
resource "aws_iam_role_policy_attachment" "ssm_policy_attachment" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMFullAccess"
  role       = aws_iam_role.bits-wilp-URLValidationElite-ec2_execution.name
}

#Configuring S3 bucket event triger for Lambda function
resource "aws_lambda_permission" "bits-wilp-s3_trigger_permission" {
  statement_id  = "AllowS3Invocation"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.bits-wilp-URLValidatorElite.arn
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.my-first-gajendran-r-terraform-s3-bucket.arn
}

resource "aws_s3_bucket_notification" "aws_s3_bucket_notification" {
  bucket = aws_s3_bucket.my-first-gajendran-r-terraform-s3-bucket.id
  lambda_function {
    lambda_function_arn = aws_lambda_function.bits-wilp-URLValidatorElite.arn
    events              = ["s3:ObjectCreated:*"]
  }
  depends_on = [aws_lambda_function.bits-wilp-URLValidatorElite]
}

#Generate .pem file
resource "tls_private_key" "my-bits-wilp-pem" {
  algorithm = "RSA"
  rsa_bits  = 4096
}

#Create a key-pair in AWS
resource "aws_key_pair" "my-bits-wilp-aws-key-pair" {
  key_name   = "Bits-EC2_key"
  public_key = tls_private_key.my-bits-wilp-pem.public_key_openssh

}
#Create a private key in local
resource "local_file" "my-bits-wilp-local-key-pair" {
  content  = tls_private_key.my-bits-wilp-pem.private_key_pem
  filename = aws_key_pair.my-bits-wilp-aws-key-pair.key_name

}