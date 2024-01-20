pipeline{

    //Declaring Variables and environment
    environment{
        AWS_ACCESS_KEY_ID	= credentials("AWS_ACCESS_KEY_ID")
        AWS_SECRET_ACCESS_KEY  = credentials("AWS_SECRET_KEY_ID")
        AWS_BUCKET_NAME = 'my-bits-wilp-jars'
        JAR_FILE_NAME  = 'URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar'
        FILE_NAME ='URLValidatorElite-'
        AWS_REGION= 'ap-south-1'
        AWS_DEFAULT_REGION ='ap-south-1'
       // TF_VAR_access_key=$AWS_ACCESS_KEY_ID
        //TF_VAR_secret_key=AWS_SECRET_ACCESS_KEY
       // S3_BUCKET='bits-wilp-terraformstat-ap-south-1'
       // TF_STATE_KEY='/terraform.tfstate'
    } // environment close

    agent any
    stages{// stages open

        //GIT Checkout
         stage('SCM checkout'){
            steps{
                echo 'URLValidatorElite: Git checkout -- starts'
                  git "https://github.com/Gaje1109/URLValidatorElite.git"
                   echo 'URLValidatorElite: Git checkout -- ends'
            }
        }
        
        //Maven clean and install
         stage('Build'){
            steps{
                echo 'URLValidatorElite: Maven clean and install -- starts'
                script{
                     dir('URLValidatorElite'){
                         bat 'mvn clean'
                         bat 'mvn install'
                     }
                }
                
                  echo 'URLValidatorElite: Maven clean and install -- ends'
            }
        }
        //Push jar to S3 bucket
         stage('Push Jar to S3'){
            steps{
                echo 'URLValidatorElite: Push Jar to S3 bucket -- starts'
                script{
                     dir('URLValidatorElite/target'){
                          def currentchildDir= pwd()
                     echo "Current Directory: ${currentchildDir}"
                     echo " jar file : ${JAR_FILE_NAME}"
                        // bat 'aws s3 cp /${FILE_NAME}${JAR_FILE_NAME} s3://${AWS_BUCKET_NAME}/'
                         
                         bat 'aws s3 cp URLValidatorElite-0.0.1-SNAPSHOT-jar-with-dependencies.jar s3://my-bits-wilp-jars/'
                        
                     }
                }
                
                  echo 'URLValidatorElite: Push Jar to S3 bucket -- ends'
            }
        }

        //Terraform Initilization
        stage('Init') {
            steps{
                 echo 'URLValidatorElite: Terraform Initialization -- starts'
                script{
                  def currentDir = pwd()

                    // Print the current directory
                    echo "Current Directory: ${currentDir}"
                      withCredentials([string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
                                 string(credentialsId: 'AWS_SECRET_KEY_ID', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                    // Move into directoy
                    dir('URLValidatorElite/Terraform') {
                      def currentchildDir= pwd()
                    echo "Current Directory: ${currentchildDir}"
                    bat 'aws sts get-caller-identity --region ap-south-1'

                 
                    // Run 'terraform init'
                   bat 'terraform init'
                                 }
                    }
                }
               echo 'URLValidatorElite: Terraform Initialization -- ends'

            }
        }
    
     //Terraform Planning
    stage('Plan'){
        steps{
             echo 'URLValidatorElite: Terraform Plan -- starts'
            script{
                        bat 'export AWS_ACCESS_KEY_ID=${AWS_ACCESS_KEY_ID}'
                        bat 'export AWS_SECRET_ACCESS_KEY=${AWS_SECRET_ACCESS_KEY}'
                        bat 'export AWS_REGION=${AWS_REGION}'
                dir('URLValidatorElite/Terraform') {
                       def currentchildDir= pwd()
                    echo "Current Directory: ${currentchildDir}" 
                      bat 'terraform plan -out tfplan'
                      bat 'terraform show -no-color tfplan > tfplan.txt'
                  
                  } 
                  
            }
                    echo 'URLValidatorElite: Terraform Plan -- ends'
        }
        }
        //Terraform Apply
         stage('Apply'){
        steps{
              echo 'URLValidatorElite: Terraform Approve -- starts'
            script{
              withCredentials([string(credentialsId: 'AWS_ACCESS_KEY_ID', variable: 'AWS_ACCESS_KEY_ID'),
                                 string(credentialsId: 'AWS_SECRET_KEY_ID', variable: 'AWS_SECRET_ACCESS_KEY')]) {
                 dir('URLValidatorElite/Terraform') {
                    def currentchildDir= pwd()
                    echo "Current Directory: ${currentchildDir}"

                    // Run 'terraform apply'
                      bat 'terraform apply -input=false tfplan'
                    }
                                 }
            }
          echo 'URLValidatorElite: Terraform Approve -- ends'
        }     
    }
    stage('Push Terraform file to S3'){
        steps{
            echo 'URLValidatorElite: Push Terraform file to S3 bucket --starts '
            //Pushing Terraform output file into S3 bucket
                 script{
                      echo 'URLValidatorElite: Push Terraform ouput file to S3 bucket -- starts'
                     dir('URLValidatorElite/Terraform'){
                          def currentchildDir= pwd()
                     echo "Current Directory: ${currentchildDir}"
                     echo " jar file : ${JAR_FILE_NAME}"
                        // bat 'aws s3 cp /${FILE_NAME}${JAR_FILE_NAME} s3://${AWS_BUCKET_NAME}/'
                         
                         bat 'aws s3 cp terraform_outputs.json s3://my-bits-wilp-jars/'
                         echo 'URLValidatorElite: Push Terraform ouput file to S3 bucket -- ends'
                     }
                }
                
                  echo 'URLValidatorElite: Push Terraform File to S3 bucket -- ends'
            }
        }
    
}// stages close
post{
    success{
        echo 'Terraform deployment successful'
    }
    failure{
         echo 'Terraform deployment failure'
    }
}
}//pipeline close