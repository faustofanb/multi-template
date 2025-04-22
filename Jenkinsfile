pipeline {
    agent any // 在任何可用的 Jenkins agent 上运行

    stages {
        stage('Checkout') { // 检出代码阶段
            steps {
                echo 'Checking out code...'
                checkout scm // Jenkins 会自动使用上面配置的 SCM 信息检出代码
            }
        }
        stage('Build') { // 构建阶段 (示例)
            steps {
                echo 'Building...'
                sh 'ls -la' // 简单地列出文件
            }
        }
        stage('Test') { // 测试阶段 (示例)
            steps {
                echo 'Testing...'
                // 添加你的测试命令
            }
        }
        stage('Deploy') { // 部署阶段 (示例)
            steps {
                echo 'Deploying...'
                // 添加你的部署命令
            }
        }
    }

    post { // Pipeline 结束后执行的操作
        always {
            echo 'Pipeline finished.'
        }
        success {
            echo 'Pipeline succeeded!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}