
local gg = {a=1}

function gg:print()
  print(self.a)
end
gg:print()

local gg2 = {}

setmetatable(gg2,gg)


for key in pairs(gg2) do
  print(key..' = '..t2[key])
end
  
  
  