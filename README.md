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



Apply playbook:
ansible-playbook -v -i hosts site.yml --tags config
