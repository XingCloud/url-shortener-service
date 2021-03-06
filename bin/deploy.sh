#!/bin/sh
line="############################################"
# Code base
code_home=/home/hadoop/git_project_home/url-shortener-service
# deploy bin home
scripts_home=${code_home}/bin/
# tomcat port
tport=$1
# Artifact Id
aid=ur
# Web interface env
env="production"
# Java binary
java_bin=/usr/java/jdk1.7.0_45/

export JAVA_HOME=${java_bin}

if [ "" = "$1" ];then
  tport=18080
  tomcat_home=/home/hadoop/catalina/apache.tomcat.te
else
  echo "User defined tomcat port found($1)"
  tport=$1
  tomcat_home=/home/hadoop/catalina/apache.tomcat.ur
fi

if [ "" = "$2" ];then
  branch=master
else
  echo "User defined branch found($2)"
  branch=$2
fi


echo "[CHECK-POINT] - Begin deploying data driller web interface."
echo ${line}
echo "[CODE-HOME] - "${code_home}
echo "[CURRENT-BRANCH] - "${branch}
echo "[ENV] - "${env}
echo "[TOMCAT-PORT] - "${tport}
echo "[JAVA-BIN] - "${java_bin}
echo ${line}
echo "[CHECK-POINT] - Update code from VCS"

cd ${code_home}
git pull
git checkout ${branch}

if [ $? -ne 0 ];then
  echo "Git update/checkout failed."
  exit 1
else
  echo "Git Update/checkout successfully."
fi

echo "[CHECK-POINT] - Packaging."
mvn -f ${code_home}/pom.xml clean package -DskipTests=true -Daid=${aid} -Denv=${env}

echo "[CHECK-POINT] - Shutdown tomcat."
sh ${tomcat_home}/bin/shutdown.sh

for((i=1;i<=10;i++));do
  proc=`ps aux | grep ${java_bin} | grep tomcat | grep -v "grep" | grep ${tport} | awk '{print $2}'`
  if [ "" = "$proc" ]
  then
    break
  else
    echo "Tomcat(${proc}) is not shutdown yet. Try again 3 second later($i)."
    sleep 3
  fi
done
proc=`ps aux | grep ${java_bin} | grep tomcat | grep -v "grep" | grep ${tport} | awk '{print $2}'`
if [ "" != "$proc" ];then
    echo "Tomcat(${proc}) is not shutdown, and it will be killed directly."
    kill -9 $proc
fi
echo "Tomcat is shutdown."

echo "[CHECK-POINT] - Clean application - ${aid}"
rm -rf ${tomcat_home}/webapps/${aid}
rm -rf ${tomcat_home}/webapps/${aid}*.war

echo "[CHECK-POINT] - Copy application - ${aid}"
cp ${code_home}/target/${aid}.war ${tomcat_home}/webapps
echo "[CHECK-POINT] - Start web server."
sh ${tomcat_home}/bin/startup.sh