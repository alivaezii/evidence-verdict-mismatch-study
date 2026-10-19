require_relative '../../../../../lib/post_provision_test'

class MySQLStretchTest < PostProvisionTest
  def initialize
    self.module_name = 'mysql_stretch_compatible'
    self.module_path = get_module_path(__FILE__)
    super
  end

  def test_module
    super
    test_local_command('mysqld executable identity verified?', 'sudo -n /tmp/secgen_mysql_identity_check.sh', 'SECGEN_MYSQL_IDENTITY_OK')
  end
end

MySQLStretchTest.new.run