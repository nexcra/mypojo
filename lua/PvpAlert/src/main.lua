local Util = require('ErwinsUtil.ErwinsUtil')
require('WowUtil.WowUtil')


-- ... 테스트
local ff = function(...)
  local a,b,c,d,e,f = ...
  local tt = {...}
  print(table.concat({...},','))
end

ff('aa',234,{cc='qwe'})