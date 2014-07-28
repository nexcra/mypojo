
local cr = coroutine.create(
  function()
    print('a')
  end
)

print(cr)

coroutine.resume(cr)