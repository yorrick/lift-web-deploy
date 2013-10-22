lift-web-deploy
===============
Simple Ansible playbook to deploy liftweb applications on Ubuntu server 13.


Restart networking when changing connection on host:
sudo service networking restart


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
cd helloworld && mvn clean && mvn package; cd -



Deploy all applications:
ansible-playbook -v -i hosts site.yml --tags deploy,test


TODO:
1) define app list in a var (group_vars/all)
1) rewrite static file paths
2) set up a DB connection using JNDI


