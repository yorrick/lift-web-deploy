lift-web-deploy
===============
Simple Ansible playbook to deploy liftweb applications on Ubuntu server 13.


Activate virtualenv:
source ~/virtualenvs/fabric/bin/activate

Test connection to nodes:
ssh yorrick@VM_IP -i ~/.ssh/app1_rsa
or
ssh-add ~/.ssh/app1_rsa
ssh VM_IP
or
ansible all -m ping (ssh VM_IP must connect for this to work)



Give sudo with no permission to yorrick user:
sudo visudo
%sudo   ALL=(ALL:ALL) NOPASSWD: ALL



Apply playbook for config:
ansible-playbook -v -i hosts site.yml --tags config



Build war for an application:
cd helloworld && mvn clean && mvn package && mv target/helloworld-1.0-SNAPSHOT.war ../roles/app/files/; cd -



Deploy all applications:
ansible-playbook -v -i hosts site.yml --tags deploy



Test site config from dev machine:
wget -O - --header="Host: localhost.helloworld.com" VM_IP



TODO:
Add a static file to helloworld project, and make nginx serve it
