VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "blacktiger"
  config.vm.box_url = "https://dl.dropboxusercontent.com/u/216137/package.box"
  config.vm.provision "shell", path: "src/test/scripts/initialize.sh"
  config.vm.network "private_network", ip: "192.168.50.2"
  config.vm.network "forwarded_port", guest: 5060, host: 5060, protocol: 'tcp'
  config.vm.network "forwarded_port", guest: 5060, host: 5060, protocol: 'udp'
  config.vm.network "forwarded_port", guest: 5038, host: 5038
end
