VAGRANTFILE_API_VERSION = "2"

Vagrant.configure(VAGRANTFILE_API_VERSION) do |config|
  config.vm.box = "blacktiger"
  config.vm.box_url = "https://dl.dropboxusercontent.com/u/216137/package.box"
  config.vm.network :forwarded_port, guest: 5038, host: 5038
  config.vm.network :forwarded_port, guest: 3306, host: 3307
end
