local function test(...)

  print('arg.n'..arg.n)
  print(arg[1])

  local sum = 0
  for i=0,10 do
    sum = sum + i
  end

  for i=0,10,1 do
    sum = sum + i
  end

  print(sum)
  print(i)

  if sum > 0 then
    print('ss')
  end

  while sum > 50 do
    sum = sum - 10
  end
  print(sum)

  local t1 = {1,2}
  local t2 = {a=1,b=2,c=3}
  t2.b = nil
  print(t1[1]) --인덱스가 1부터 시작함
  print(t2)
  for key in pairs(t2) do
    print(key..' = '..t2[key])
  end

  function t2:add()
    --self.a = 11
    print("애드0" .. t2.a)
  end

  t2.aa = function()
  --print("리무브 " .. t2.a)
  end


  t2.add()
  t2.aa()

  local aa = function(...)
    local a,b = ...  -- ... 로 받으면 가변인자이다.
    print (a)
    print (b)
  end
  aa(1,2)


end

test(1,3)
