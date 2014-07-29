print ('=== ErwinsUtil loaded ===')

-- =========== static 상수 ================
u = {}

-- 간단한 테이블 내용물 검색기
u.info = function(table)
  if(type(table) ~= 'table') then
    print('- IS NOT TABLE : ' .. type(table) .. ' ' .. table)
    return
  end  
  local size = #table
  if(size==0) then
    print('- key-value size =  0')
  end
  for key in pairs(table) do
    local value = table[key]
    local type = type(value)
    if(type=='function') then  value = 'function' end 
    print('- ' .. key .. ' = ' .. value)
  end
end