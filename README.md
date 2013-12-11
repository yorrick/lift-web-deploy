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


Set up a ssh connection with a key-pair (no password)
ssh-keygen -t rsa
Copy public key to ~/.ssh/authorized_keys (chmod 700 for .ssh dir, 600 for authorized_keys)

Modify your hosts ~/.ssh/config files for 

host ubuntu-server-vm
    Hostname 192.168.56.2
    IdentityFile ~/.ssh/app1_rsa



Ssh connection
==============

Activate virtualenv:
source ~/virtualenvs/fabric/bin/activate

Test connection to nodes:

Add those lines in ~/.ssh/config

host ubuntu-server-vm
    User yorrick
    Hostname 192.168.56.2
    IdentityFile ~/.ssh/app1_rsa

Then do 

ssh-add ~/.ssh/app1_rsa
ssh ubuntu-server-vm

ansible all -m ping (ssh VM_IP must connect for this to work)



Ansible
=======

Apply playbook for config:
ansible-playbook -v -i hosts site.yml --tags config


Build war for an application:
cd helloworld && mvn clean && mvn package && mv target/helloworld_toto-1.0-SNAPSHOT.war ../roles/app/files/; cd -


Deploy all applications:
ansible-playbook -v -i hosts site.yml --tags deploy,test


TODO
====
 - use add line in file instead of copying everything in ansible
 - test thread pool in jetty (with continuations in lift)
 - see how to connect a broker (JMS by example) to actors
 - see how to implement caching in lift with memcached (and a kind of decorator)
 - integrate bootstrap in lift app
 - set jetty logs path for each app http://webtide.intalio.com/2011/08/sifting-logs-in-jetty-with-logback/
    - copy jars in /usr/share/jetty8/etc/lib/logging/
    										/ext/
    - add libs in /usr/share/jetty8/etc/start.config ??
    - add 2 config files in /usr/share/jetty8/etc/
    - add config xml files to /usr/share/jetty8/etc/jetty.conf
    - create /usr/share/jetty8/resources/logback.xml with configuration we want
 - create a webapp user and put war in it, use this location in jetty and nginx


