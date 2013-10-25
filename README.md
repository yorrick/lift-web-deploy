lift-web-deploy
===============
Simple Ansible playbook to deploy liftweb applications on Ubuntu server 13.



Manage VM
=========
VBoxManage controlvm "ubuntu server" poweroff; VBoxManage snapshot "ubuntu server" restore "initial-snapshot" && VBoxManage startvm "ubuntu server"


VM must be configured in virtualbox to have 2 network interfaces, one in NATm the other in Host Only.
Set a static ip address to Host Only interface:

/etc/network/interfaces
-----------------------
# The loopback network interface
auto lo
iface lo inet loopback

# The primary network interface
auto eth0
iface eth0 inet dhcp

auto eth1
iface eth1 inet static
  address 192.168.56.2
  netmask 255.255.255.0


Give sudo with no permission to yorrick user:
sudo visudo
%sudo   ALL=(ALL:ALL) NOPASSWD: ALL


Ansible
=======

Activate virtualenv:
source ~/virtualenvs/fabric/bin/activate

Test connection to nodes:
ssh yorrick@VM_IP -i ~/.ssh/app1_rsa
or
ssh-add ~/.ssh/app1_rsa
ssh VM_IP
or
ansible all -m ping (ssh VM_IP must connect for this to work)


Apply playbook for config:
ansible-playbook -v -i hosts site.yml --tags config


Build war for an application:
cd helloworld && mvn clean && mvn package; cd -


Deploy all applications:
ansible-playbook -v -i hosts site.yml --tags deploy,test


TODO
====
 - check props overriding in lift
 - set up virtual hosting in jetty using contexts (did not work before...) or web-jetty.xml file.

